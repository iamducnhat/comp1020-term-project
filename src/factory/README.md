# Factory Package

## Các class cần tạo:
- `BeverageFactory.java` - Tạo đồ uống dynamically (Factory Pattern)

## Design Pattern:
- **Factory Pattern**: `createBeverage(type, name, size, price)` trả về Coffee/Tea tuỳ theo type
- Tránh hardcode object creation, dễ mở rộng thêm loại mới
