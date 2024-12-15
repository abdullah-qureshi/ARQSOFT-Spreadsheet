# 📊 ARQSOFT Spreadsheet Project

## Overview
A simple command-line spreadsheet application developed as part of the Software Architecture course based on JAVA.

---

## Core Features

### Basic Operations
- Text-based interface for intuitive spreadsheet manipulation
- Read and write operations for cell management
- Robust error handling for invalid operations

### Formula Support
- Mathematical expression evaluation
- Cell referencing (e.g., `=A1+B2`)
- Real-time calculation updates

### Error Handling
- Circular dependency detection
- Invalid formula protection

---

## Project Architecture

### Core Components

#### 1. Spreadsheet
```
Main class that handles:
├── Cell management
├── Operation coordination
├── Save/Load operations
└── Evaluation for input commands
```

#### 2.  Cell
```
Handles:
├── Content storage (values/formulas)
├── State management
└── Dependency tracking
```

#### 3. Formula
```
Expression processor that manages:
├── Mathematical evaluations
├── Cell reference resolution
└── Calculation updates
```

#### 4. Parser
```
A Shunting Yard algorithm that provides:
├── Formula Tree generation
```

### System Flow
```mermaid
graph LR
    CLI[Command Line Interface] --> Spreadsheet
    Spreadsheet --> Cell
    Cell --> Parser
    Parser <--> Formula Tree
```

---

### Formula Examples
```
=A1 + B2           # Addition
=SUM(A1:A5)        # Range sum
=AVERAGE(B1:B10)   # Average of range
=A1 * (B2 + C3)    # Complex formula
```

---

## 🛠️ Development Status
This academic project emphasizes:
- Clean Architecture principles
- SOLID design patterns
- Efficient data structures

---
