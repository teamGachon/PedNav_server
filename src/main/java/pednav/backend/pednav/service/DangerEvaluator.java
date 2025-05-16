package pednav.backend.pednav.service;

import pednav.backend.pednav.dto.PartialData;

@FunctionalInterface
public interface DangerEvaluator {
    String evaluateDanger(PartialData data);
}
