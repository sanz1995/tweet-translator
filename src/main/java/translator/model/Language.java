package translator.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Language {

    @Id
    private String language;


    public Language() {
    }

    public Language(String language) {
        this.language = language;
    }


    public String getLanguage() {
        return language;
    }


}
