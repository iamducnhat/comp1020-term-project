# Controller Layer

## Các class cần tạo:
- `OrderController.java` - Quản lý đơn hàng (dùng Queue + PriorityQueue)
- `CustomerController.java` - Quản lý khách hàng (dùng HashMap)
- `InventoryController.java` - Quản lý kho (delegate tới InventoryManager Singleton)
- `VoucherController.java` - Quản lý voucher (dùng HashMap)

## Data Structures:
- **Queue (LinkedList)**: xử lý order FIFO
- **PriorityQueue**: ưu tiên order VIP
- **HashMap**: tra cứu O(1) cho customer và voucher
- **Stack**: undo/redo (trong ActionHistory ở util/)
