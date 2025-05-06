using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.ValueObjects
{
    public readonly struct Money
    {
        public decimal Amount { get; }
        public string Currency { get; }

        public Money(decimal amount, string currency)
        {
            if (amount < 0) throw new ArgumentException("Negative import");
            Amount = amount;
            Currency = currency;
        }

        public Money Add(Money other)
        {
            if (other.Currency == Currency)
            {
                return new Money(Amount + other.Amount, Currency);
            }
            else
            {
                throw new ArgumentException("Cannot add different money types");
            }

        }

    }
}
