package ch.zhaw.freelancer4u.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.freelancer4u.model.Company;
import ch.zhaw.freelancer4u.model.CompanyCreateDTO;
import ch.zhaw.freelancer4u.model.MailInformation;
import ch.zhaw.freelancer4u.repository.CompanyRepository;
import ch.zhaw.freelancer4u.service.CompanyService;
import ch.zhaw.freelancer4u.service.MailValidatorService;
import ch.zhaw.freelancer4u.service.UserService;
@RestController
@RequestMapping("/api")
public class CompanyController {
    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    CompanyService companyService;

    @Autowired
    UserService userService;
    
    @Autowired
    MailValidatorService mailValidatorService;

    @PostMapping("/company")
    public ResponseEntity<Company> createCompany(
        @RequestBody CompanyCreateDTO fDTO) {
        if (!userService.userHasRole("admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        
        // Validate email
        MailInformation mailInfo = mailValidatorService.validateEmail(fDTO.getEmail());
        if (!mailInfo.isFormat() || mailInfo.isDisposable() || !mailInfo.isDns()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        String nextId = companyService.getNextId();
        Company fDAO = new Company(nextId, fDTO.getName(), fDTO.getEmail());
        Company f = companyRepository.save(fDAO);
        return new ResponseEntity<>(f, HttpStatus.CREATED);
    } 

    @GetMapping("/company")
    public ResponseEntity<Page<Company>> getAllCompanies(
        @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
        @RequestParam(required = false, defaultValue = "20") Integer pageSize) {

        int effectivePageNumber = Math.max(0, pageNumber - 1);
        Page<Company> allCompanies = companyRepository.findAll(PageRequest.of(effectivePageNumber, pageSize));
        return new ResponseEntity<>(allCompanies, HttpStatus.OK);
    }

    @GetMapping("/company/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable String id) {
        Optional<Company> company = companyRepository.findById(id);
        if (company.isPresent()) {
            return new ResponseEntity<>(company.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
