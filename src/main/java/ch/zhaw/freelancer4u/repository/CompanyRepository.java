package ch.zhaw.freelancer4u.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ch.zhaw.freelancer4u.model.Company;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends MongoRepository<Company, String> {
    Optional<Company> findById(String id);
    Page<Company> findAll(Pageable pageable);
}
