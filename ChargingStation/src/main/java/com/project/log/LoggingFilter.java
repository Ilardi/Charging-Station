package com.project.log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;


/*
Questo componente abilita il logging di tutte le richieste fatte alle web API
(livello DEBUG). Le informazioni vengono salvate nel file "logs/myapp.log".
Nel file di configurazione si è impostata la dimensione massima del file
a 10 MB; se questa venisse superata si attiva la rotation, ovvero i log più vecchi
vengono archiviati. I messaggi di sistema sono invece stati configurati al
livello ERROR.
*/

@Configuration
public class LoggingFilter {
    
	@Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter
          = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setIncludeHeaders(true);  
        filter.setAfterMessagePrefix("After request ");
        return filter;
    }
    
}