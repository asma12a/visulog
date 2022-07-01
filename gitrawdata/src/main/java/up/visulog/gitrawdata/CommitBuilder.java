package up.visulog.gitrawdata;

// Importation de la classe Date : 

import java.util.Date;

/**
 * Classe de gestion d'un commit: Elle permet de créer un commit et d'apporter
 * plusieurs changement à ce dernier (L'auteur, date, description et la branche
 * à partir de laquelle il a été mergé)
 * 
 * @author Asma MOKEDDES
 * @version 1.0
 */
public class CommitBuilder {
    /**
     * L'ID du commit. Cet ID n'est pas modifiable.
     */

    private final String id;

    /**
     * L'auteur du commit. Cet auteur est changeable.
     * 
     * @see CommitBuilder#setAuthor(String)
     */

    private String author;

    /**
     * La date du commit. Cette date est changeable.
     * 
     * @see CommitBuilder#setDate(String)
     */

    private Date date;

    /**
     * La description d'un commit. Cette description est modifiable.
     * 
     * @see CommitBuilder#setDescription(String)
     */

    private String description;

    /**
     * Le nom de la branche à partir de laquelle le commit a été mergé. Cette
     * branche est changeable.
     * 
     * @see CommitBuilder#setMergedFrom(String)
     */

    private String mergedFrom;

    /**
     * Constructeur CommitBuilder.
     * <p>
     * A la construction d'un objet CommitBuilder avec un identifiant unique .
     * </p>
     * 
     * @param id          L'identifiant unique du Commit.
     * @param author      L'auteur d'un Commit .
     * @param date        La date de la publication d'un Commit.
     * @param description La description d'un Commit.
     * 
     * @see CommitBuilder#id
     * @see CommitBuilder#author
     * @see CommitBuilder#date
     * @see CommitBuilder#description
     */

    public CommitBuilder(String id) {
        this.id = id;
    }

    /**
     * Mis à jour l'auteur d'un commit
     * 
     * @param author
     * @return {CommitBuilder} - Une instance de CommitBuilder.
     */

    public CommitBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    /**
     * Mis à jour de la date de publication d'un commit.
     * 
     * @param date
     * @return {CommitBuilder} - Une instance de CommitBuilder.
     */

    public CommitBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    /**
     * Mis à jour de la description d'un commit.
     * 
     * @param description
     * @return {CommitBuilder} - Une instance de CommitBuilder.
     */
    public CommitBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Mis à jour le MergedFrom d'un commit.
     * 
     * @param mergedFrom
     * @return {CommitBuilder} - Une instance de CommitBuilder.
     */

    public CommitBuilder setMergedFrom(String mergedFrom) {
        this.mergedFrom = mergedFrom;
        return this;
    }

    /**
     * Création d'un commit.
     * 
     * @return {CommitBuilder} - Une instance de CommitBuilder.
     */

    public Commit createCommit() {
        return new Commit(id, author, date, description, mergedFrom);
    }
}
