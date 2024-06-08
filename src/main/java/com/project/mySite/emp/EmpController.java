package com.project.mySite.emp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EmpController {

    @Autowired
    private EmpService empService;

    @GetMapping("emp")
    @ResponseBody
    public EmpDefinition addEmp() {
        EmpDefinition empDefinition = new EmpDefinition();
        empDefinition.seteName("test1");
        empDefinition.setEmpno(1000);
        empDefinition.setJob("CEO");

        empService.addEmp(empDefinition);
        return empDefinition;
    }



}
