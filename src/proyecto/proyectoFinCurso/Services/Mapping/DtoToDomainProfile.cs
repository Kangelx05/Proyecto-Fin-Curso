using AutoMapper;
using Domain.Entities;
using Domain.ValueObjects;
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
            CreateMap<int, Quantity>()
            .ConvertUsing(src => new Quantity(src, 0));

            CreateMap<Quantity, int>()
            .ConvertUsing(q => q.Value);
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
