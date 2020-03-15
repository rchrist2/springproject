package myproject.services;

import myproject.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public void updateRole(String roleName, String roleDesc, int roleId){
        roleRepository.updateRole(roleName, roleDesc, roleId);
    }
}
