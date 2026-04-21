# ☕ VinTony Coffee Shop - POS & Loyalty System

**COMP1020 – Spring 2026 Term Project**  
**Team VinTony – Group 5**

## Team Members
| Name | Student ID | Role |
|------|-----------|------|
| Nguyễn Đức Nhật | V202502368 | Team Leader |
| Đinh Nguyễn Gia Khánh | V202502398 | Developer |
| Khúc Nhật Minh | V202502361 | Developer |
| Lê Đức Huy | V202502747 | Developer |
| Nguyễn Duy Hiếu | V202502909 | Developer |

## Project Description
A Java desktop POS (Point of Sale) and Loyalty System for a coffee shop. The system manages orders, customers, inventory, and vouchers.

## Tech Stack
- **Language:** Java 17+
- **GUI:** Java Swing
- **Architecture:** MVC (Model-View-Controller)

## Project Structure
```
src/
├── Main.java                          # Entry point
├── model/                             # Model layer
│   ├── beverage/
│   │   ├── Beverage.java              # Abstract base class
│   │   ├── Coffee.java                # Extends Beverage
│   │   ├── Tea.java                   # Extends Beverage
│   │   └── Size.java                  # Enum (S/M/L)
│   ├── addon/
│   │   ├── ToppingDecorator.java      # Decorator Pattern (abstract)
│   │   └── Topping.java              # Concrete Decorator
│   ├── order/
│   │   ├── Order.java                 # Order with Comparable
│   │   └── OrderStatus.java           # Enum
│   ├── customer/
│   │   └── Customer.java              # Customer with loyalty points
│   ├── voucher/
│   │   └── Voucher.java               # Discount voucher
│   ├── inventory/
│   │   ├── Ingredient.java            # Ingredient model
│   │   └── InventoryManager.java      # Singleton Pattern
│   └── menu/
│       ├── MenuNode.java              # Tree node
│       └── MenuTree.java             # Tree structure
├── controller/                        # Controller layer
│   ├── OrderController.java           # Queue + PriorityQueue
│   ├── CustomerController.java        # HashMap lookup
│   ├── InventoryController.java       # Singleton delegation
│   └── VoucherController.java         # HashMap lookup
├── view/                              # View layer (Swing)
│   ├── MainFrame.java                 # Main window with tabs
│   ├── OrderPanel.java                # Order management UI
│   ├── CustomerPanel.java             # Customer management UI
│   ├── InventoryPanel.java            # Inventory management UI
│   └── VoucherPanel.java              # Voucher management UI
├── factory/
│   └── BeverageFactory.java           # Factory Pattern
└── util/
    ├── ActionHistory.java             # Stack (undo/redo)
    └── SortUtil.java                  # Sorting & Binary Search
```

## Design Patterns
| Pattern | Applied To | Purpose |
|---------|-----------|---------|
| **Factory** | `BeverageFactory` | Dynamic beverage creation |
| **Decorator** | `ToppingDecorator`, `Topping` | Add toppings without modifying base class |
| **Singleton** | `InventoryManager` | Single global inventory instance |
| **MVC** | Entire architecture | Separation of concerns |

## Data Structures & Algorithms
| DS/Algorithm | Applied To | Complexity |
|-------------|-----------|------------|
| **Queue (FIFO)** | Order processing | O(1) |
| **PriorityQueue** | VIP order processing | O(log n) |
| **HashMap** | Customer & Inventory lookup | O(1) avg |
| **Stack** | Undo/redo operations | O(1) |
| **Tree** | Menu hierarchy | O(log n) |
| **Sorting** | Orders by time/price, Customers by points | O(n log n) |
| **Binary Search** | Customer lookup by phone | O(log n) |

## OOP Principles
- **Encapsulation:** Private fields with getters/setters
- **Inheritance:** `Coffee`, `Tea` extend `Beverage`
- **Polymorphism:** `calculatePrice()` implemented differently per subclass
- **Abstraction:** `Beverage` abstract class defines common interface

## How to Compile & Run
```bash
# Compile
javac -d out src/**/*.java src/*.java

# Run
java -cp out Main
```

## Project Timeline
| Phase | Description | Start | End |
|-------|------------|-------|-----|
| 1 | Planning & Analysis | Mar 12 | Mar 26 |
| 2 | Requirements Definition | Mar 27 | Apr 2 |
| 3 | Design & Prototyping | Apr 3 | Apr 16 |
| 4 | Implementation | Apr 17 | May 7 |
| 5 | Refactoring & Features | May 8 | May 14 |
| 6 | Testing & Deployment | May 15 | May 21 |
| 7 | Finalization & Docs | May 22 | May 28 |
