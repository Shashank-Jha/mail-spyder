package com.spyder.api.repository;

import com.spyder.api.entity.Emails;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmailRepository implements PanacheRepository<Emails> { }
