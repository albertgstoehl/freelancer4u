package ch.zhaw.freelancer4u.repository;

import ch.zhaw.freelancer4u.model.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends MongoRepository<Company, String> {
}
