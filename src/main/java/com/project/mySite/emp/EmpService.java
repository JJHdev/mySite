package com.project.mySite.emp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpService {

    @Autowired
    private EmpDefinitionRepository empDefinitionRepository;

    public EmpDefinition addEmp(EmpDefinition empDefinition) {
        return empDefinitionRepository.create(empDefinition);
    }
}
