package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.model.Group;
import com.fiuba_groups.fiuba_groups_back.repository.GroupRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    @Autowired private GroupRepository groupRepository;

    public Optional<Group> getGroup(Long groupId) {
        return groupRepository.findById(groupId);
    }
}
