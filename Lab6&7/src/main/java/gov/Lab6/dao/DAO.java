package gov.Lab6.dao;

import gov.Lab6.data.DataRepresentationModel;
import gov.Lab6.data.builder.DataBuilder;

import java.util.Optional;

public interface DAO<T extends DataRepresentationModel> {
    public Optional<T> get(int id);
    public DAO<T> remove(T item);
    public DAO<T> update(T item);
    public DAO<T> add(DataBuilder<T> builder);
}