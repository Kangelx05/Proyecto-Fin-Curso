using AutoMapper;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Services.Mapping
{
    public static class AutoMapperConfig
    {
        // Exponemos el IMapper como singleton estático
        public static IMapper Mapper { get; private set; }

        public static void RegisterMappings()
        {
            // 1) Crea la configuración y añade tu perfil
            var config = new MapperConfiguration(cfg =>
            {
                // si tu assembly de Services solo tiene ese perfil, este es suficiente:
                cfg.AddProfile<DtoToDomainProfile>();

                // si estuvieras usando varios perfiles:
                // cfg.AddProfiles(typeof(DtoToDomainProfile).Assembly);
            });

            // 2) (opcional) valida la configuración
            try
            {
                config.AssertConfigurationIsValid();
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.ToString());
            }
            

            // 3) Crea el mapper
            Mapper = config.CreateMapper();
        }
    }
}
