
using Domain.Entities;
using Services.Mapping;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Services.ApplicationServices
{
    public class UsersService
    {

        private ApiClient apiClient;
        public UsersService() {


            
           

        }

        public async Task<User> getUser()
        {
            var dto = await apiClient.GetUserByIdAsync(1);

            User user = AutoMapperConfig.Mapper.Map<User>(dto);
            return user;
        }

    }
}
