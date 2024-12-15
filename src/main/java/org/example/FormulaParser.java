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
    // Set operator precedence here
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
            // Convert to Reverse Polish Notation
            List<Token> rpn = convertToRPN(tokens);
            if (rpn.isEmpty()) {
                System.out.println("Error: Could not convert to RPN");
                return null;
            }
            // Build the node tree
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
                if (currentToken.length() > 0) {
                    tokens.add(createToken(currentToken.toString()));
                    currentToken.setLength(0);
                }
                continue;
            }
            
            // Check if character is an operator (+,-,*,/) or parenthesis
            if (isOperatorOrParenthesis(c)) {
                if (currentToken.length() > 0) {
                    tokens.add(createToken(currentToken.toString()));
                    currentToken.setLength(0);
                }
                
                TokenType type;
                if (c == '(') {
                    type = TokenType.LEFT_PAREN;
                } else if (c == ')') {
                    type = TokenType.RIGHT_PAREN; 
                } else {
                    type = TokenType.OPERATOR;
                }
                
                tokens.add(new Token(type, String.valueOf(c)));
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
        // Check if it's a cell reference like "A1" or range like "A1:B2"
        boolean isCellRef = value.matches("[A-Z][0-9]+") || 
                           value.matches("[A-Z][0-9]+:[A-Z][0-9]+");
        if (isCellRef) {
            return new Token(TokenType.CELL_REFERENCE, value);
        }
        
        // Check if it's a number (positive, negative or decimal)
        try {
            Double.parseDouble(value);
            return new Token(TokenType.NUMBER, value);
        } catch (NumberFormatException e) {
            // Not a number, continue checking
        }
        
        // Check if it's a supported function name
        String upperValue = value.toUpperCase();
        if (SUPPORTED_FUNCTIONS.contains(upperValue)) {
            return new Token(TokenType.FUNCTION, upperValue);
        }
        
        // If we get here, the token is invalid
        throw new IllegalArgumentException("Invalid token: " + value);
    }

    private static List<Token> convertToRPN(List<Token> tokens) {
        List<Token> output = new ArrayList<>();
        List<Token> operatorStack = new ArrayList<>();
        
        for (Token token : tokens) {
            if (token.type == TokenType.NUMBER || token.type == TokenType.CELL_REFERENCE) {
                output.add(token);
                continue;
            }
            
            if (token.type == TokenType.FUNCTION || token.type == TokenType.LEFT_PAREN) {
                operatorStack.add(token);
                continue;
            }
            
            if (token.type == TokenType.RIGHT_PAREN) {
                while (!operatorStack.isEmpty() && 
                       getLast(operatorStack).type != TokenType.LEFT_PAREN) {
                    output.add(removeLast(operatorStack));
                }
                
                if (!operatorStack.isEmpty()) {
                    removeLast(operatorStack);
                }
                
                if (!operatorStack.isEmpty() && getLast(operatorStack).type == TokenType.FUNCTION) {
                    output.add(removeLast(operatorStack));
                }
                continue;
            }
            
            // Handle operators
            if (token.type == TokenType.OPERATOR) {
                while (!operatorStack.isEmpty() && 
                       getLast(operatorStack).type == TokenType.OPERATOR &&
                       getPrecedence(getLast(operatorStack)) >= getPrecedence(token)) {
                    output.add(removeLast(operatorStack));
                }
                operatorStack.add(token);
            }
        }
        
        while (!operatorStack.isEmpty()) {
            Token token = removeLast(operatorStack);
            if (token.type == TokenType.LEFT_PAREN || token.type == TokenType.RIGHT_PAREN) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            output.add(token);
        }
        
        return output;
    }
    
    // Helper methods to treat ArrayList like a stack
    private static Token getLast(List<Token> list) {
        return list.get(list.size() - 1);
    }
    
    private static Token removeLast(List<Token> list) {
        return list.remove(list.size() - 1);
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

    private static boolean isOperatorOrParenthesis(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')';
    }

    private static int determineFunctionArgCount(String functionName) {
        switch (functionName.toUpperCase()) {
            case "SUMA":   
            case "MIN":   
            case "MAX":    
            case "PROMEDIO": 
                return -1; 
            default:
                throw new IllegalArgumentException("Unknown function: " + functionName);
        }
    }

    // Example method to handle range parsing (simplified)
    private static List<FormulaNode> parseRange(String range) {
        List<FormulaNode> cellNodes = new ArrayList<>();

        String[] parts = range.split(":");
        if (parts.length == 1) {
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
