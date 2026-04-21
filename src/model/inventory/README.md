# Model Layer - Inventory Package

## Các class cần tạo:
- `Ingredient.java` - Nguyên liệu (tên, đơn vị, số lượng)
- `InventoryManager.java` - Quản lý kho (Singleton Pattern)

## Design Pattern:
- **Singleton**: `InventoryManager.getInstance()` - chỉ có 1 instance duy nhất

## Data Structure:
- **HashMap**: ingredient name -> Ingredient, lookup O(1)
