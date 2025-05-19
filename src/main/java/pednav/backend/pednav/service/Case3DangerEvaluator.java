package pednav.backend.pednav.service;

import pednav.backend.pednav.dto.Case3DangerRequest;

@FunctionalInterface
public interface Case3DangerEvaluator {
    String evaluateDanger(Case3DangerRequest request);
}
