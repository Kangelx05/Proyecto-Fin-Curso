using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.ValueObjects
{
    public readonly struct Quantity
    {
        public int Value { get; }
        public int MinValue { get; }

        public Quantity(int value, int minValue)
        {
            if (value < minValue)
                throw new ArgumentException("Must be at least "+ minValue, nameof(value));
            Value = value;
            MinValue = minValue;
        }

        public Quantity Add(Quantity other)
            => new Quantity(Value + other.Value, MinValue);
    }
}
