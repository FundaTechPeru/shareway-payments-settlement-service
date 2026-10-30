package com.fundatech.shareway.paymentsettlement.bdd;

import io.cucumber.spring.ScenarioScope;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Estado compartido entre las clases de steps durante un único escenario.
 */
@Component
@ScenarioScope
public class ScenarioContext {

    private static final Pattern UUID_ALIAS = Pattern.compile("\\{uuid:(\\d+)}");

    private String bearerToken;
    private String apiKey;
    private MvcResult lastResult;
    private final Map<String, String> variables = new HashMap<>();

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public MvcResult getLastResult() {
        return lastResult;
    }

    public void setLastResult(MvcResult lastResult) {
        this.lastResult = lastResult;
    }

    public void put(String name, Object value) {
        variables.put(name, String.valueOf(value));
    }

    /**
     * Sustituye los marcadores {@code {uuid:N}} por el UUID determinista del alias N y los marcadores
     * {@code {nombre}} por las variables guardadas en el escenario.
     */
    public String resolve(String text) {
        String resolved = UUID_ALIAS.matcher(text)
                .replaceAll(match -> TestIds.uuid(Long.parseLong(match.group(1))).toString());
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            resolved = resolved.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return resolved;
    }
}
