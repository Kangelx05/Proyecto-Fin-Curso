using AutoMapper;
using Domain.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Services.Mapping
{
    public class DtoToDomainProfile : Profile
    {
        public DtoToDomainProfile()
        {
            // Product
            CreateMap<ProductResponse, Product>();
            CreateMap<Product, ProductRequest>();

            // User
            CreateMap<UserResponse, User>();
            CreateMap<User, UserRequest>();

            // Table
            CreateMap<TableResponse, Table>();
            CreateMap<Table, TableRequest>();

            // Order
            CreateMap<OrderResponse, Order>();
            CreateMap<Order, OrderRequest>();

            // Order details
            CreateMap<OrderDetailResponse, OrderDetails>();
            CreateMap<OrderDetails, OrderDetailRequest>();

            // Order state history
            CreateMap<OrderStateHistoryResponse, OrderHistoryEntry>();
            CreateMap<OrderHistoryEntry, OrderStateHistoryRequest>();

            // Inventory
            CreateMap<InventoryResponse, InventoryItem>();
            CreateMap<InventoryItem, InventoryRequest>();
        }
    }
}
