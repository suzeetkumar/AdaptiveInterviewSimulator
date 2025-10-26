package com.AdaptiveInterviewSimulator.repo;

import com.AdaptiveInterviewSimulator.model.Embedding;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmbeddingRepository extends JpaRepository<Embedding, Long> {
    List<Embedding> findBySourceTypeAndSourceId(String sourceType, Long sourceId);
}
