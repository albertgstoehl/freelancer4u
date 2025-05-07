package ch.zhaw.freelancer4u.service;

import org.springframework.stereotype.Service;
import ch.zhaw.freelancer4u.repository.CompanyRepository;
import ch.zhaw.freelancer4u.model.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class CompanyService {
    @Autowired
    CompanyRepository companyRepository;

    public boolean companyExists(String companyId) {
        return companyRepository.existsById(companyId);
    }

    public String getNextId() {
        Company lastCompany = companyRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
            .stream()
            .findFirst()
            .orElse(null);
        
        if (lastCompany == null) {
            return "1";
        }
        
        try {
            int lastId = Integer.parseInt(lastCompany.getId());
            return String.valueOf(lastId + 1);
        } catch (NumberFormatException e) {
            // If the last ID is not a number, start from 1
            return "1";
        }
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public void createCompany(String name, String email) {
        Company company = new Company(name, email);
        companyRepository.save(company);
    }
}