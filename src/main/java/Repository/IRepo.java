package Repository;

import Domain.Entity;

import java.io.IOException;
import java.util.List;

/**
 * Generic repository interface for CRUD operations
 */
public interface IRepo<T extends Entity> {
    void add(T entity) throws RepositoryException;

    void update(T entity) throws RepositoryException;

    void delete(T entity) throws RepositoryException, IOException;

    T findById(int id) throws RepositoryException;

    List<T> findAll() throws RepositoryException;

    void refresh() throws RepositoryException;
}