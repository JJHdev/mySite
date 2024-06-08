package com.project.mySite.emp;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.aspectj.apache.bcel.classfile.SourceFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EmpDefinitionRepository {
    @Autowired
    private EntityManagerFactory emf;
    private EntityManager em;

    public EmpDefinition create(EmpDefinition empDefinition) {

        try {
            em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            try{em.persist(empDefinition);
                System.out.println("eName : " + empDefinition.geteName());
                System.out.println("Job : " + empDefinition.getJob());
                System.out.println("Empno : " + empDefinition.getEmpno());
                tx.commit();
            }
            catch(Exception e){
                tx.rollback();
                System.out.println(e.getMessage());
            }finally{
                em.clear();
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

        return empDefinition;

    }

}
