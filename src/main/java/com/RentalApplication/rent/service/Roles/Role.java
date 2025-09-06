package com.RentalApplication.rent.service.Roles;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Role {


        public static final String Admin  = "Admin";
        public static final String Owner  = "Owner";
        public static final String Client = "Client";

        public static final String PREFIX = "ROLE_"; // if you build authorities like ROLE_Owner, etc.


}
