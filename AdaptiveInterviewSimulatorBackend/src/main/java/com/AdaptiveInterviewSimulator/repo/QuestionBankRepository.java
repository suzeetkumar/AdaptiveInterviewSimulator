package com.AdaptiveInterviewSimulator.repo;

import com.AdaptiveInterviewSimulator.model.QuestionBank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {
    List<QuestionBank> findByCategory(String category);
}
