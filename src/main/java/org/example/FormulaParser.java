package org.example;

import java.util.*;

public class FormulaParser {
    private enum TokenType {
        NUMBER, 
        CELL_REFERENCE, 
        OPERATOR, 
        FUNCTION, 
        LEFT_PAREN, 
        RIGHT_PAREN, 
        ARGUMENT_SEPARATOR
    }

    // Represents a token during parsing
    private static class Token {
        TokenType type;
        String value;

        Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    // Operator precedence and associativity
    private static final Map<String, Integer> OPERATOR_PRECEDENCE = Map.of(
        "+", 1,
        "-", 1,
        "*", 2,
        "/", 2
    );

    // Supported functions
    private static final Set<String> SUPPORTED_FUNCTIONS = Set.of(
        "SUMA", "MIN", "MAX", "PROMEDIO"
    );

    public static FormulaNode parse(String formula) {
        if (formula == null || formula.trim().isEmpty()) {
            System.out.println("Error: Formula cannot be null or empty");
            return null;
        }

        // Remove leading '=' if present
        if (formula.startsWith("=")) {
            formula = formula.substring(1);
        }

        // Tokenize the input with error handling
        List<Token> tokens;
        try {
            tokens = tokenize(formula);
            if (tokens.isEmpty()) {
                System.out.println("Error: No valid tokens found");
                return null;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }

        try {
            // Convert to Reverse Polish Notation (RPN)
            List<Token> rpn = convertToRPN(tokens);
            if (rpn.isEmpty()) {
                System.out.println("Error: Could not convert to RPN");
                return null;
            }

            // Build the node tree from RPN
            FormulaNode node = buildNodeTree(rpn);
            if (node == null) {
                System.out.println("Error: Could not build syntax tree");
                return null;
            }
            return node;

        } catch (Exception e) {
            System.out.println("Error processing formula: " + e.getMessage());
            return null;
        }
    }

    private static List<Token> tokenize(String formula) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        
        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);
            
            if (Character.isWhitespace(c)) {
                // Skip whitespace
                if (currentToken.length() > 0) {
                    tokens.add(createToken(currentToken.toString()));
                    currentToken.setLength(0);
                }
                continue;
            }
            
            // Check for operators and parentheses
            if ("+-*/()".indexOf(c) != -1) {
                // Add previous token if exists
                if (currentToken.length() > 0) {
                    tokens.add(createToken(currentToken.toString()));
                    currentToken.setLength(0);
                }
                
                // Add operator or parenthesis
                tokens.add(new Token(
                    c == '(' ? TokenType.LEFT_PAREN :
                    c == ')' ? TokenType.RIGHT_PAREN :
                    TokenType.OPERATOR, 
                    String.valueOf(c)
                ));
                continue;
            }
            
            // Check for argument separator
            if (c == ';') {
                if (currentToken.length() > 0) {
                    tokens.add(createToken(currentToken.toString()));
                    currentToken.setLength(0);
                }
                tokens.add(new Token(TokenType.ARGUMENT_SEPARATOR, ";"));
                continue;
            }
            
            // Build token
            currentToken.append(c);
        }
        
        // Add last token if exists
        if (currentToken.length() > 0) {
            tokens.add(createToken(currentToken.toString()));
        }
        
        return tokens;
    }

    private static Token createToken(String value) {
        // Determine token type
        if (value.matches("^[A-Za-z][0-9]+:[A-Za-z][0-9]+$") || value.matches("^[A-Za-z][0-9]+$")) {
            // Cell reference (both single cell and range)
            return new Token(TokenType.CELL_REFERENCE, value);
        }
        
        if (value.matches("^-?\\d+(\\.\\d+)?$")) {
            // Numeric value
            return new Token(TokenType.NUMBER, value);
        }
        
        if (SUPPORTED_FUNCTIONS.contains(value.toUpperCase())) {
            // Function
            return new Token(TokenType.FUNCTION, value.toUpperCase());
        }
        
        throw new IllegalArgumentException("Invalid token: " + value);
    }

    private static List<Token> convertToRPN(List<Token> tokens) {
        List<Token> output = new ArrayList<>();
        Deque<Token> operatorStack = new ArrayDeque<>();
        
        for (Token token : tokens) {
            switch (token.type) {
                case NUMBER, CELL_REFERENCE:
                    output.add(token);
                    break;
                
                case FUNCTION:
                    operatorStack.push(token);
                    break;
                
                case LEFT_PAREN:
                    operatorStack.push(token);
                    break;
                
                case RIGHT_PAREN:
                    while (!operatorStack.isEmpty() && operatorStack.peek().type != TokenType.LEFT_PAREN) {
                        output.add(operatorStack.pop());
                    }
                    if (!operatorStack.isEmpty() && operatorStack.peek().type == TokenType.LEFT_PAREN) {
                        operatorStack.pop(); // Remove left parenthesis
                    }
                    if (!operatorStack.isEmpty() && operatorStack.peek().type == TokenType.FUNCTION) {
                        output.add(operatorStack.pop()); // Add function to output
                    }
                    break;
                
                case OPERATOR:
                    while (!operatorStack.isEmpty() && 
                           operatorStack.peek().type == TokenType.OPERATOR && 
                           getPrecedence(operatorStack.peek()) >= getPrecedence(token)) {
                        output.add(operatorStack.pop());
                    }
                    operatorStack.push(token);
                    break;
                
                case ARGUMENT_SEPARATOR:
                    // Skip argument separators in RPN notation
                    break;
            }
        }
        
        while (!operatorStack.isEmpty()) {
            Token token = operatorStack.pop();
            if (token.type == TokenType.LEFT_PAREN || token.type == TokenType.RIGHT_PAREN) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            output.add(token);
        }
        
        return output;
    }

    private static int getPrecedence(Token token) {
        return OPERATOR_PRECEDENCE.getOrDefault(token.value, 0);
    }

    private static FormulaNode buildNodeTree(List<Token> rpn) {
        Deque<FormulaNode> nodeStack = new ArrayDeque<>();
        
        for (Token token : rpn) {
            switch (token.type) {
                case NUMBER:
                    nodeStack.push(new ValueNode(Double.parseDouble(token.value)));
                    break;
                
                case CELL_REFERENCE:
                    if (token.value.contains(":")) {
                        // This is a range, expand it into individual cells
                        List<FormulaNode> cellNodes = parseRange(token.value);
                        // For functions like SUMA, we can add all cells to the stack
                        cellNodes.forEach(nodeStack::push);
                    } else {
                        // Single cell reference
                        nodeStack.push(new CellNode(token.value));
                    }
                    break;
                
                case OPERATOR:
                    if (nodeStack.size() < 2) {
                        throw new IllegalArgumentException("Invalid formula: insufficient operands");
                    }
                    FormulaNode right = nodeStack.pop();
                    FormulaNode left = nodeStack.pop();
                    List<FormulaNode> children = Arrays.asList(left, right);
                    FormulaNode operatorNode = switch (token.value) {
                        case "+" -> new AdditionNode(children);
                        case "-" -> new SubtractionNode(children);
                        case "*" -> new MultiplicationNode(children);
                        case "/" -> new DivisionNode(children);
                        default -> throw new IllegalArgumentException("Unknown operator: " + token.value);
                    };
                    nodeStack.push(operatorNode);
                    break;
                
                case FUNCTION:
                    // For functions, collect all available nodes from the stack
                    List<FormulaNode> functionChildren = new ArrayList<>();
                    while (!nodeStack.isEmpty()) {
                        functionChildren.add(0, nodeStack.pop());
                    }
                    
                    if (functionChildren.isEmpty()) {
                        throw new IllegalArgumentException("Function must have at least one argument: " + token.value);
                    }
                    
                    // Create appropriate function node
                    FormulaNode functionNode = switch (token.value) {
                        case "SUMA" -> new AdditionNode(functionChildren);
                        case "MIN" -> new MinNode(functionChildren);
                        case "MAX" -> new MaxNode(functionChildren);
                        case "PROMEDIO" -> new MeanNode(functionChildren);
                        default -> throw new IllegalArgumentException("Unknown function: " + token.value);
                    };
                    
                    nodeStack.push(functionNode);
                    break;
            }
        }
        
        if (nodeStack.size() != 1) {
            throw new IllegalArgumentException("Invalid formula: too many operands");
        }
        
        return nodeStack.pop();
    }

    private static int determineFunctionArgCount(String functionName) {
        switch (functionName.toUpperCase()) {
            case "SUMA":   // SUM (Range or multiple numbers)
            case "MIN":    // MIN (Range or multiple numbers)
            case "MAX":    // MAX (Range or multiple numbers)
            case "PROMEDIO": // AVERAGE (Range or multiple numbers)
                return -1;  // -1 signifies variable argument count
            default:
                throw new IllegalArgumentException("Unknown function: " + functionName);
        }
    }

    // Example method to handle range parsing (simplified)
    private static List<FormulaNode> parseRange(String range) {
        List<FormulaNode> cellNodes = new ArrayList<>();

        String[] parts = range.split(":");
        if (parts.length == 1) {
            // Single cell reference (e.g., "A1")
            cellNodes.add(new CellNode(parts[0]));
        } else if (parts.length == 2) {
            String startCell = parts[0];
            String endCell = parts[1];

            // Extract column letters and row numbers
            char startCol = startCell.charAt(0);
            int startRow = Integer.parseInt(startCell.substring(1));
            char endCol = endCell.charAt(0);
            int endRow = Integer.parseInt(endCell.substring(1));

            // Iterate through the rectangular range
            for (char col = startCol; col <= endCol; col++) {
                for (int row = startRow; row <= endRow; row++) {
                    String cellRef = String.valueOf(col) + row;
                    cellNodes.add(new CellNode(cellRef));
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid range format: " + range);
        }

        return cellNodes;
    }
}
