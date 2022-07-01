package up.visulog.gitrawdata;

import java.io.BufferedReader;
//Un BufferedReader est une sorte de pile  qui prends en argument un "file reader" et qui empile ses lignes
import java.io.IOException;
//ce module cherche à gérer les exceptions
import java.io.InputStream;
//Sert à lire des données binaires
import java.io.InputStreamReader;
//Transforme des données binaires en texte
import java.io.File;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.eclipse.jgit.annotations.NonNull;
import org.eclipse.jgit.api.errors.GitAPIException;
import java.nio.file.Path;
import java.util.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.diff.DiffConfig;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.IndexDiff.StageState;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.FollowFilter;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
//En gros, permet de gerer le cas object = null

// bib date

import java.util.Date;
import java.util.Locale;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Commit {
  // FIXME: (some of) these fields could have more specialized types than String
  public final String id;
  public final Date date;
  public final String author;
  public final String description;
  public final String mergedFrom;

  public Commit(String id, String author, Date date, String description, String mergedFrom) {
    this.id = id;
    this.author = author;
    this.date = date;
    this.description = description;
    this.mergedFrom = mergedFrom;
  }

  public static ArrayList<HashMap<String, Integer>> pourcentageCommentaires(Path chemin) {
    ArrayList<HashMap<String, Integer>> statistiques = new ArrayList<HashMap<String, Integer>>();
    HashMap<String, Integer> lignesCodes = new HashMap<String, Integer>();
    HashMap<String, Integer> commentaires = new HashMap<String, Integer>();
    HashMap<String, Integer> lignesCodesSupprimees = new HashMap<String, Integer>();
    HashMap<String, Integer> lignesCodesSupprimeesPurifie = new HashMap<String, Integer>();
    HashMap<String, Integer> lignesCodesPurifie = new HashMap<String, Integer>();
    HashMap<String, Integer> commentairesPurifie = new HashMap<String, Integer>();
    HashMap<String, Integer> lignesVides = new HashMap<String, Integer>();
    HashMap<String, Integer> lignesVidesPurifie = new HashMap<String, Integer>();
    HashMap<String, Integer> lignesCodeAbsoluPurifie = new HashMap<String, Integer>();
    try {
      List<Commit> gitlog = Commit.parseLogFromCommand(chemin);
      int n = gitlog.size();
      int nbLignesAjoutees = 0;
      int nbEspaces = 0;
      // int nbCommentaires = 0;

      for (int i = 0; i < n - 1; i++) {
        Commit recent = gitlog.get(i);
        Commit ancien = gitlog.get(i + 1);
        String auteur = recent.author;
        String[] tab = auteur.split(" ");
        String mail = tab[tab.length - 1].substring(1, tab[tab.length - 1].length() - 1);
        // System.out.println(mail);
        // System.out.println(recent.author);
        if (!lignesCodes.containsKey(mail))
          lignesCodes.put(mail, 0);
        if (!commentaires.containsKey(mail))
          commentaires.put(mail, 0);
        if (!lignesVides.containsKey(mail))
          lignesVides.put(mail, 0);
        if (!lignesCodesSupprimees.containsKey(mail))
          lignesCodesSupprimees.put(mail, 0);
        // TODO : limiter la date
        ArrayList<String> diffs = Commit.diff_Commits(recent, ancien, chemin.toString());
        for (String s : diffs) {
          // System.out.println(s);
          Scanner sc = new Scanner(s);
          sc.useDelimiter("\n");
          while (sc.hasNext()) {
            String ligne = sc.next();
            if (ligne.charAt(0) == '+') {

              if (ligne.length() == 1) {

                // System.out.println(ligne);
                lignesCodes.put(mail, lignesCodes.get(mail) + 1);
                lignesVides.put(mail, lignesVides.get(mail) + 1);
              } else if (ligne.length() >= 2 && ligne.charAt(1) != '+') {

                lignesCodes.put(mail, lignesCodes.get(mail) + 1);
                String[] mots = ligne.split("\\s+");
                if (mots[0].length() > 1 && mots[0].charAt(1) == '/') {
                  commentaires.put(mail, commentaires.get(mail) + 1);
                } else if (mots[0].length() > 1 && mots[0].charAt(1) == '*') {
                  commentaires.put(mail, commentaires.get(mail) + 1);
                } else if (mots.length >= 2 && mots[1].charAt(0) == '/') {
                  commentaires.put(mail, commentaires.get(mail) + 1);
                } else if (mots.length >= 2 && mots[1].charAt(0) == '*') {
                  commentaires.put(mail, commentaires.get(mail) + 1);
                }
              } else {
              }

            } else if (ligne.charAt(0) == '-') {
              if (ligne.length() == 1) {
                lignesCodesSupprimees.put(mail, lignesCodesSupprimees.get(mail) + 1);
                lignesVides.put(mail, lignesVides.get(mail) - 1);
              } else if (ligne.length() >= 2 && ligne.charAt(1) != '-') {
                lignesCodesSupprimees.put(mail, lignesCodesSupprimees.get(mail) + 1);
                String[] mots = ligne.split("\\s+");
                if (mots[0].length() > 1 && mots[0].charAt(1) == '/') {
                  commentaires.put(mail, commentaires.get(mail) - 1);
                  // System.out.println(mots[0]);
                } else if (mots[0].length() > 1 && mots[0].charAt(1) == '*') {
                  commentaires.put(mail, commentaires.get(mail) - 1);
                } else if (mots.length >= 2 && mots[1].charAt(0) == '/') {
                  commentaires.put(mail, commentaires.get(mail) - 1);
                  // System.out.println(ligne);
                } else if (mots.length >= 2 && mots[1].charAt(0) == '*') {
                  commentaires.put(mail, commentaires.get(mail) - 1);
                }
                // System.out.println(ligne);
              }
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (String name : lignesCodes.keySet()) {
      String[] parties = name.split("@");
      if (!parties[0].equals("adegorre"))
        lignesCodesPurifie.put(parties[0], lignesCodes.get(name));
    }
    for (String name : commentaires.keySet()) {
      String[] parties = name.split("@");
      if (!parties[0].equals("adegorre"))
        commentairesPurifie.put(parties[0], commentaires.get(name));
    }
    for (String name : lignesVides.keySet()) {
      String[] parties = name.split("@");
      if (!parties[0].equals("adegorre"))
        lignesVidesPurifie.put(parties[0], lignesVides.get(name));
    }
    for (String name : lignesCodesSupprimees.keySet()) {
      String[] parties = name.split("@");
      if (!parties[0].equals("adegorre"))
        lignesCodesSupprimeesPurifie.put(parties[0], lignesCodesSupprimees.get(name));
    }
    for (String name : lignesCodesSupprimees.keySet()) {
      String[] parties = name.split("@");
      if (!parties[0].equals("adegorre"))
        lignesCodeAbsoluPurifie.put(parties[0], lignesCodes.get(name) - lignesCodesSupprimees.get(name));
    }

    statistiques.add(lignesCodesPurifie);
    statistiques.add(lignesCodesSupprimeesPurifie);
    statistiques.add(lignesCodeAbsoluPurifie);
    statistiques.add(commentairesPurifie);
    statistiques.add(lignesVidesPurifie);

    return statistiques;
  }

  // Cette fonction renvoie le résultat de git diff entre deux commits
  public static ArrayList<String> diff_Commits(Commit recent, Commit ancien, String chemin) throws IOException {
    // File file = new File("origin/develop");
    // File file = new File("/home/yves/projetS3Info/visulog/.git");
    File file = new File(chemin + "/.git");
    Repository repository = new FileRepositoryBuilder().setGitDir(file).build();
    ObjectId commitRecent = repository.resolve(recent.id + "^{tree}");
    ObjectId commitAncien = repository.resolve(ancien.id + "^{tree}");
    try (ObjectReader reader = repository.newObjectReader()) {
      CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
      newTreeIter.reset(reader, commitRecent);
      CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
      oldTreeIter.reset(reader, commitAncien);
      Git git = new Git(repository);
      List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      DiffFormatter df = new DiffFormatter(out);
      df.setRepository(repository);
      ArrayList<String> diffText = new ArrayList<String>();
      for (DiffEntry diff : diffs) {
        df.format(diff);
        RawText r = new RawText(out.toByteArray());
        r.getLineDelimiter();
        diffText.add(out.toString());
        out.reset();
      }
      return diffText;
    } catch (GitAPIException e) {
      e.printStackTrace();
      return null;
    }

  }

  // TODO: factor this out (similar code will have to be used for all git
  // commands)

  public static BufferedReader process(ProcessBuilder pb, String exception) {
    Process p;
    try {
      p = pb.start();
      // lance la commande git log sur le terminal, builder.start() active le terminal
    } catch (IOException e) {
      throw new RuntimeException(exception, e);
    }
    InputStream input = p.getInputStream();
    // récupère ce que renvoie le terminal sous forme de données binaires
    BufferedReader br = new BufferedReader(new InputStreamReader(input));
    return br;
    // InputStreamReader transforme les données binaires en texte
    // puis BufferedReader fait une sorte de pile avec le fichier text
  }

  public static List<Commit> parseLogFromCommand(Path gitPath) {
    ProcessBuilder builder = new ProcessBuilder("git", "log").directory(gitPath.toFile());
    BufferedReader reader = process(builder, "Error running \"git log\".");
    return parseLog(reader);
    // On transforme cette pile en une list de commit grace à parselog et
    // parseCommit
  }

  public static List<Commit> parseLog(BufferedReader reader) {
    var result = new ArrayList<Commit>();
    Optional<Commit> commit = parseCommit(reader);
    while (commit.isPresent()) {
      result.add(commit.get());
      // Si commit n'est pas null : on l'ajoute à la liste des commits
      commit = parseCommit(reader);
    }
    return result;
  }

  // Méthode auxiliaire :
  // TODO : Il faut utiliser LocalDateTime au lieu de LocalDate
  public static Date convertToDateViaInstant(LocalDateTime dateToConvert) {
    // TODO : j'ai changé ici pour convertir java.time.LocalDateTime en
    // java.util.Date
    return java.util.Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
  }

  /**
   * Parses a log item and outputs a commit object. Exceptions will be thrown in
   * case the input does not have the proper format. Returns an empty optional if
   * there is nothing to parse anymore.
   */
  public static Optional<Commit> parseCommit(BufferedReader input) {
    try {

      var line = input.readLine();
      if (line == null)
        return Optional.empty(); // if no line can be read, we are done reading the buffer
      var idChunks = line.split(" ");
      // divise la ligne en un tableau de mots
      if (!idChunks[0].equals("commit"))
        parseError();
      // on vérifie que c'est bien un commit
      var builder = new CommitBuilder(idChunks[1]);
      // juste après le mot commit, il y a l'identifiant du commit dans le terminal
      line = input.readLine();
      // Ici, on va lire une ligne puis la diviser en deux au niveau du " : "
      while (!line.isEmpty()) {
        var colonPos = line.indexOf(":");
        var fieldName = line.substring(0, colonPos);
        var fieldContent = line.substring(colonPos + 1).trim();
        // le trim ici enlève les espaces superflues

        switch (fieldName) {
          // On remplit l'objet commit avec ce qui est lu grace au commit builder
          case "Author":
            builder.setAuthor(fieldContent);
            break;
          case "Merge":
            builder.setMergedFrom(fieldContent);
            break;
          case "Date":
            // @author asma

            try {

              Locale.setDefault(Locale.US);
              DateTimeFormatter dateFormater = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy ", Locale.US);
              LocalDateTime currentLocaldate = LocalDateTime.parse(fieldContent, dateFormater);
              Date finalCurrentDate = convertToDateViaInstant(currentLocaldate);
              builder.setDate(finalCurrentDate);

              break;

            } catch (Exception e) {
              System.out.println("ERROR ! " + e);
              // TODO: handle exception
            }
          default: // TODO: warn the user that some field was ignored
        }
        line = input.readLine(); // prepare next iteration
        if (line == null)
          parseError(); // end of stream is not supposed to happen now (commit data incomplete)
      }

      // now read the commit message per se
      // On aurait pu tout mettre sur une ligne ici mais ce serait illisible
      var description = input.lines() // get a stream of lines to work with
          .takeWhile(currentLine -> !currentLine.isEmpty()) // take all lines until the first empty one (commits are
                                                            // separated by empty lines). Remark: commit messages are
                                                            // indented with spaces, so any blank line in the message
                                                            // contains at least a couple of spaces.
          .map(String::trim) // remove indentation
          .reduce("", (accumulator, currentLine) -> accumulator + currentLine); // concatenate everything
      builder.setDescription(description);
      return Optional.of(builder.createCommit());
    } catch (IOException e) {
      parseError();
    }
    return Optional.empty(); // this is supposed to be unreachable, as parseError should never return
  }

  // Helper function for generating parsing exceptions. This function *always*
  // quits on an exception. It *never* returns.
  private static void parseError() {
    throw new RuntimeException("Wrong commit format.");
  }

  @Override
  public String toString() {
    return "Commit{" + "id='" + id + '\'' + (mergedFrom != null ? ("mergedFrom...='" + mergedFrom + '\'') : "") + // TODO:
                                                                                                                  // find
                                                                                                                  // out
                                                                                                                  // if
                                                                                                                  // this
                                                                                                                  // is
                                                                                                                  // the
                                                                                                                  // only
                                                                                                                  // optional
                                                                                                                  // field
        ", date='" + date + '\'' + ", author='" + author + '\'' + ", description='" + description + '\'' + '}';
  }

  public static HashMap<String, String> parseDiffFromCommand(String id1, String id2, Path gitPath) {
    ProcessBuilder pb = new ProcessBuilder("git", "diff", "--shortstat", id1, id2).directory(gitPath.toFile());
    BufferedReader br = process(pb, "Error running  in method parseDiffFromCommand ");

    LinkedList<String> numberOfModifications = modifications(br);
    HashMap<String, String> modification = new HashMap();
    modification.put("numberOfAddedLines", numberOfModifications.get(1));
    modification.put("numberOfDeletedLines", numberOfModifications.get(2));
    modification.put("numberOfModifiedFiles", numberOfModifications.get(0));
    return modification;
  }

  public static LinkedList<String> modifications(BufferedReader reader) {
    Scanner sc = new Scanner(reader);
    sc.useDelimiter(",");
    LinkedList<String> l = new LinkedList<>();
    while (sc.hasNext()) {
      String[] tmp = sc.next().split(" ");
      l.add(tmp[1]);
    }
    return l;
  }

  public static HashMap<String, Integer> parseBlameFromCommand(String filePath, Path gitPath) {
    ProcessBuilder pb = new ProcessBuilder("git", "blame", filePath).directory(gitPath.toFile());
    BufferedReader br = process(pb, "Error running in method parseBlameFromCommand ");

    return authorLines(br);
  }

  public static HashMap<String, Integer> authorLines(BufferedReader reader) {
    HashMap<String, Integer> authorLinesNumber = new HashMap<String, Integer>();
    Scanner sc = new Scanner(reader);
    sc.useDelimiter("\n");
    while (sc.hasNext()) {
      String tmp = author(sc.next());
      if (!authorLinesNumber.containsKey(tmp) && tmp != "(Not Committed") {
        authorLinesNumber.putIfAbsent(tmp, 1);
      } else {
        Integer value = authorLinesNumber.get(tmp);
        authorLinesNumber.replace(tmp, value + 1);
      }
    }
    return authorLinesNumber;
  }

  public static String author(String str) {
    String[] tmp = str.split(" ");
    String nameAuthor = tmp[1].substring(1) + " " + tmp[2];
    return nameAuthor;
  }

  public static LinkedList<String> parseLsFilesFromCommand(Path gitPath) {
    ProcessBuilder pb = new ProcessBuilder("git", "ls-files").directory(gitPath.toFile());
    BufferedReader br = process(pb, "Error running in method parseLsFilesFromCommand ");
    return parseLsFiles(br);
  }

  public static LinkedList<String> parseLsFiles(BufferedReader reader) {
    Scanner sc = new Scanner(reader);
    LinkedList<String> tmp = new LinkedList<>();
    while (sc.hasNext()) {
      tmp.add(sc.next());
    }
    return tmp;
  }

}
