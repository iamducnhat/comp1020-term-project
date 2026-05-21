# VinTony Coffee POS & Loyalty System

**COMP1020 Spring 2026 — Team VinTony, Group 5**

VinTony is a Java Swing desktop POS and loyalty system for a coffee shop. The app demonstrates OOP, MVC, design patterns, and the required data structures from the project proposal.

## Features

- Order management with active cart, add/remove items, undo/redo, cancel queued orders, and process next order.
- VIP order priority queue plus regular FIFO queue.
- Customer registration, update/delete, phone lookup, loyalty point award/redeem, and leaderboard.
- Voucher creation, validation, checkout discount, one-time use marking, and removal.
- Inventory tracking with default ingredients, add/restock/consume/remove, low-stock alerts, and automatic stock deduction when orders are processed.
- Menu and drink creation through Coffee/Tea inheritance, BeverageFactory, and topping Decorator pattern.

## Project Structure

```text
src/
├── Main.java
├── controller/
│   ├── OrderController.java
│   ├── OrderQueue.java
│   ├── RegularOrderQueue.java
│   ├── VIPOrderQueue.java
│   ├── CustomerController.java
│   ├── InventoryController.java
│   └── VoucherController.java
├── factory/
│   └── BeverageFactory.java
├── model/
│   ├── addon/
│   ├── beverage/
│   ├── customer/
│   ├── inventory/
│   ├── menu/
│   ├── order/
│   └── voucher/
├── util/
│   ├── ActionHistory.java
│   └── SortUtil.java
└── view/
    ├── MainFrame.java
    ├── OrderPanel.java
    ├── CustomerPanel.java
    ├── InventoryPanel.java
    └── VoucherPanel.java
```

## Concepts Covered

| Requirement | Implementation |
|---|---|
| Encapsulation | Customer, Order, Voucher, Ingredient |
| Inheritance | Coffee and Tea extend Beverage |
| Polymorphism | `calculatePrice()` and ingredient requirements per beverage type |
| Abstraction | `Beverage` abstract class and `OrderQueue` interface |
| Factory Pattern | `BeverageFactory` |
| Decorator Pattern | `ToppingDecorator`, `Topping` |
| Singleton Pattern | `InventoryManager` |
| MVC | `model`, `controller`, and `view` packages |
| Queue | `RegularOrderQueue` with `LinkedList` |
| PriorityQueue | `VIPOrderQueue` |
| HashMap | Customer, voucher, and inventory lookup |
| Stack | `ActionHistory` undo/redo |
| Tree | `MenuTree`, `MenuNode` |
| Sorting/Search | `SortUtil` |

## Build And Run

```bash
./build.sh
```

## Run Feature Smoke Test

This runs the proposal features from code without opening the Swing application:

```bash
RUN_APP=0 JAVA_HOME=/path/to/jdk-18.0.2.1/Contents/Home ./build.sh
javac -encoding UTF-8 -cp out -d out-test tests/VinTonyFeatureSmokeTest.java
java -cp out:out-test VinTonyFeatureSmokeTest
```

Or manually:

```bash
find src -name "*.java" -print0 | xargs -0 javac -encoding UTF-8 -sourcepath src -d out
java -cp out Main
```

If Java 21/25 crashes on macOS 26 with a HotSpot `SIGBUS` / `CodeHeap::allocate` error, use a stable JDK such as Temurin 18.0.2.1 and run:

```bash
JAVA_HOME=/path/to/jdk-18.0.2.1/Contents/Home ./build.sh
```
