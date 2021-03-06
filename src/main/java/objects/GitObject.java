package objects;

import repository.Repository;

/*
 * Format of git object -
 * "[objectype] [objectLength]\0[objectData]"
 */
public class GitObject {
    Repository repository;
    public GitObject(Repository repository, String data) {
        this.repository = repository;
        if (data != null) {
            this.deserialize(data);
        }
    }

    public String serialize() {
        throw new RuntimeException("No implementation available!");
    }

    public void deserialize(String data) {
        throw new RuntimeException("No implementation available!!");
    }

    public String getType() {
        throw new RuntimeException("No implementation available");
    }

    public Repository getRepository() {
        return this.repository;
    }
}
