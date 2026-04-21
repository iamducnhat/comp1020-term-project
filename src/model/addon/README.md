# Model Layer - Add-on Package (Decorator Pattern)

## Các class cần tạo:
- `ToppingDecorator.java` - Abstract decorator, extends Beverage
- `Topping.java` - Concrete decorator (thêm topping vào đồ uống)

## Design Pattern:
- **Decorator Pattern**: Thêm topping mà không sửa base class
- ToppingDecorator wraps một Beverage object
- `calculatePrice()` = giá đồ uống + giá topping
