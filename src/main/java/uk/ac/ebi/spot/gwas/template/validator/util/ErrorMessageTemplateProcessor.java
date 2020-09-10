package uk.ac.ebi.spot.gwas.template.validator.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import uk.ac.ebi.spot.gwas.template.validator.config.ErrorMessagesConfig;
import uk.ac.ebi.spot.gwas.template.validator.config.ErrorType;
import uk.ac.ebi.spot.gwas.template.validator.domain.ErrorMessage;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class ErrorMessageTemplateProcessor {

    @Autowired
    private ErrorMessagesConfig errorMessagesConfig;

    private TemplateEngine templateEngine;

    @PostConstruct
    public void initialize() {
        templateEngine = new TemplateEngine();
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        templateEngine.setTemplateResolver(templateResolver);
    }

    public List<String> processGenericError(String errorType, String context) {
        List<String> result = new ArrayList<>();
        Map<String, String> contextMap = new HashMap<>();
        if (context != null) {
            contextMap.put(ErrorType.CONTEXT, context);
        }
        result.add(processMessage(errorType, contextMap));
        return result;
    }

    public List<String> process(Pair<String, List<String>> generalError, Map<Pair<String, ErrorMessage>, List<Integer>> errorMap) {
        List<String> result = new ArrayList<>();
        Map<String, String> contextMap;
        for (Pair<String, ErrorMessage> messagePair : errorMap.keySet()) {
            List<Integer> rows = errorMap.get(messagePair);

            contextMap = new HashMap<>();
            contextMap.put(ErrorType.CTX_ROW, ValidationUtil.compress(rows).toString());
            contextMap.put(ErrorType.CTX_COLUMN, messagePair.getLeft());
            if (messagePair.getRight().getValue() != null) {
                contextMap.put(ErrorType.CTX_VALUE, messagePair.getRight().getValue());
            }
            String messageKey = messagePair.getRight().getSubtype() != null ?
                    messagePair.getRight().getType() + "-" + messagePair.getRight().getSubtype() :
                    messagePair.getRight().getType();
            result.add(processMessage(messageKey, contextMap));
        }
        if (generalError != null) {
            contextMap = new HashMap<>();
            String head = generalError.getLeft();
            if (!generalError.getRight().isEmpty()) {
                if (head.endsWith("!")) {
                    head = head.substring(0, head.length() - 1);
                    contextMap.put(ErrorType.CTX_VALUE, StringUtils.join(generalError.getRight(), ";"));
                } else {
                    List<Integer> converted = ValidationUtil.convert(generalError.getRight());
                    contextMap.put(ErrorType.CTX_ROW, ValidationUtil.compress(converted).toString());
                }
            }
            result.add(processMessage(head, contextMap));
        }

        /*
        List<Integer> rows = orderRows(generalErrorMap, errorMap);
        Map<String, String> contextMap;
        for (int row : rows) {
            List<String> messages = new ArrayList<>();
            if (errorMap.containsKey(row)) {
                for (String cell : errorMap.get(row).keySet()) {
                    ErrorMessage errorMessage = errorMap.get(row).get(cell);

                    contextMap = new HashMap<>();
                    contextMap.put(ErrorType.CTX_ROW, Integer.toString(row));
                    contextMap.put(ErrorType.CTX_COLUMN, cell);
                    if (errorMessage.getValue() != null) {
                        contextMap.put(ErrorType.CTX_VALUE, errorMessage.getValue());
                    }
                    String messageKey = errorMessage.getSubtype() != null ? errorMessage.getType() + "-" + errorMessage.getSubtype() : errorMessage.getType();
                    messages.add(processMessage(messageKey, contextMap));
                }
            }
            if (generalErrorMap.containsKey(row)) {
                contextMap = new HashMap<>();
                contextMap.put(ErrorType.CTX_ROW, Integer.toString(row));
                messages.add(processMessage(generalErrorMap.get(row), contextMap));
            }

            result.add(StringUtils.join(messages, "; "));
        }

         */
        return result;
    }

    public String processMessage(String messageKey, Map<String, String> contextMap) {
        Context context = new Context();
        if (contextMap != null) {
            for (String key : contextMap.keySet()) {
                String value = contextMap.get(key);
                context.setVariable(key, value);
            }
        }

        return templateEngine.process(errorMessagesConfig.getErrorMessages().get(messageKey), context);
    }

    private List<Integer> orderRows(Map<Integer, String> generalErrorMap, Map<Integer, Map<String, ErrorMessage>> errorMap) {
        List<Integer> result = new ArrayList<>();
        Map<Integer, Boolean> sortMap = new TreeMap<>();
        for (int i : generalErrorMap.keySet()) {
            sortMap.put(i, true);
        }
        for (int i : errorMap.keySet()) {
            sortMap.put(i, true);
        }
        for (int i : sortMap.keySet()) {
            result.add(i);
        }
        return result;
    }
}
