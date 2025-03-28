package Repository;

import Domain.Entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RepoMemory<T extends Entity> implements IRepo<T> {
    protected final List<T> repo = new ArrayList<>();
    private final AtomicInteger currentId = new AtomicInteger(1);

    @Override
    public void add(T entity) throws RepositoryException {
        if (entity == null) {
            throw new RepositoryException("Entity cannot be null");
        }

        if (entity.getId() == 0) {
            entity.setId(currentId.getAndIncrement());
        }

        if (findById(entity.getId()) != null) {
            throw new RepositoryException("Entity with ID " + entity.getId() + " already exists");
        }

        repo.add(entity);
    }

    @Override
    public void update(T entity) throws RepositoryException {
        if (entity == null) {
            throw new RepositoryException("Entity cannot be null");
        }

        T existing = findById(entity.getId());
        if (existing == null) {
            throw new RepositoryException("Entity with ID " + entity.getId() + " not found");
        }

        int index = repo.indexOf(existing);
        repo.set(index, entity);
    }

    @Override
    public void delete(T entity) throws RepositoryException, IOException {
        if (!repo.remove(entity)) {
            throw new RepositoryException("Entity not found for deletion");
        }
    }

    @Override
    public T findById(int id) throws RepositoryException {
        return repo.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(repo);
    }

    @Override
    public void refresh() throws RepositoryException {

    }
}