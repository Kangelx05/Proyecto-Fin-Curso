using Domain.Entities;
using Domain.Enumerations;
using Domain.ValueObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Services
{
    public interface IPricingService
    {
        PaymentType paymentType { get; set; }
        Money CalculateTotal(Table table);
        Money CalculateSubtotal(Table table);
        Money CalculateTaxes(Table table);
        Money ApplyDiscounts(Table table);
    }
}
