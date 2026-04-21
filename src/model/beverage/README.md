# Model Layer - Beverage Package

## Các class cần tạo:
- `Beverage.java` - Abstract base class (Abstraction)
- `Coffee.java` - Kế thừa Beverage (Inheritance)
- `Tea.java` - Kế thừa Beverage (Inheritance)
- `Size.java` - Enum (S, M, L)

## OOP cần áp dụng:
- **Abstraction**: `Beverage` là abstract class, có abstract method `calculatePrice()`
- **Inheritance**: `Coffee`, `Tea` kế thừa `Beverage`
- **Polymorphism**: Mỗi subclass implement `calculatePrice()` khác nhau
- **Encapsulation**: Private fields + getters/setters
