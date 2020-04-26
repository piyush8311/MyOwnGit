package myowngit;

import org.ini4j.Wini;
import utils.Directory;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * This class represents a repository and all the branches it has.
 */
public class Repository {
    private String gitdir;
    private Wini configReader;
    private Directory repositoryDirectory;

    public Repository(String path, boolean force) throws Exception {
        initializeAndValidateGitDir(path, force);
        initializeAndValidateConfig(force);
    }

    private void initializeAndValidateGitDir(String path, boolean force) {
        this.gitdir = Paths.get(path).resolve(".mygit").toString();
        this.repositoryDirectory = new Directory(this.gitdir);

        File file = new File(this.gitdir);
        if (!force && !file.isDirectory()) {
            throw new RuntimeException("Not a git repository " + this.gitdir);
        }
    }

    private void initializeAndValidateConfig(boolean force) throws Exception {
        Optional<String> configPath = repositoryDirectory.joinFileWithRepoPath(false, "config");

        File configFile = null;
        if (configPath.isPresent())
            configFile  = new File(configPath.get());

        if (configFile != null && configFile.exists()) {
            this.configReader = new Wini(configFile);
        } else if (!force) {
            throw new RuntimeException("Config file does not exist in .mygit");
        }

        if (!force) {
            int ver = this.configReader.get("core", "repositoryformatversion", int.class);
            if (ver != 0) {
                throw new RuntimeException("Unsupported repositoryformatversion " + ver);
            }
        }
    }

    /**
     * Will only be used by init command handler function
     */
    public void createRepository() throws Exception {
        createRequiredDirs();
        createRequiredFiles();
    }

    private void createRequiredDirs() {
        createGitDir();
        createDir("branches");
        createDir("objects");
        createDir("refs", "tags");
        createDir("refs", "heads");
    }

    private void createGitDir() {
        File file = new File(this.gitdir);
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new RuntimeException("Not a directory " + this.gitdir);
            }
            if (file.listFiles().length != 0) {
                throw new RuntimeException("Not an empty directory");
            }
        } else {
            file.mkdirs();
        }
    }

    private void createDir(String... dirName) {
        Optional<String> path = repositoryDirectory.joinDirWithRepoPath(true, dirName);
        assert(path.isPresent());
        assert(new File(path.get()).isDirectory());
    }

    private void createRequiredFiles() throws Exception {
        createDescriptionFile();
        createHEADFile();
        createConfigFile();
    }

    private void createDescriptionFile() throws Exception {
        File file = new File(repositoryDirectory.joinFileWithRepoPath(true,"description").get());
        file.createNewFile();
        FileWriter descriptionFileWriter = new FileWriter(file);
        descriptionFileWriter.write("Unnamed repository; edit this file 'description' to name the repository.\n");
        descriptionFileWriter.close();
    }

    private void createHEADFile() throws Exception {
        File file = new File(repositoryDirectory.joinFileWithRepoPath(true,"HEAD").get());
        file.createNewFile();
        FileWriter headFileWriter = new FileWriter(file);
        headFileWriter.write("ref: refs/heads/master\n");
        headFileWriter.close();
    }

    private void createConfigFile() throws Exception {
        File configFile = new File(repositoryDirectory.joinFileWithRepoPath(true,"config").get());
        configFile.createNewFile();
        this.configReader = new Wini(configFile);
        initializeDefaultConfig();
        this.configReader.store();
    }

    private void initializeDefaultConfig() {
        this.configReader.add("core");
        this.configReader.add("core", "repositoryformatversion", "0");
        this.configReader.add("core", "filemode", "false");
        this.configReader.add("core", "bare", "false");
    }
}
