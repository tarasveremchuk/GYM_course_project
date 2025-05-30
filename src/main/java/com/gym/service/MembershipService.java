package com.gym.service;

import com.gym.model.Membership;
import com.gym.dao.impl.MembershipDao;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Slf4j
public class MembershipService {
    
    private final MembershipDao membershipDao;
    
    public MembershipService() {
        this.membershipDao = new MembershipDao();
    }
    

    public MembershipService(MembershipDao membershipDao) {
        this.membershipDao = membershipDao;
    }
    

    public List<Membership> getAllMemberships() {
        log.info("Retrieving all memberships");
        return membershipDao.findAll();
    }
    

    public Optional<Membership> getMembershipById(Long id) {
        log.info("Finding membership with ID: {}", id);
        return membershipDao.findById(id);
    }

    public List<Membership> getActiveMembershipsByClientId(Long clientId) {
        log.info("Finding active memberships for client ID: {}", clientId);
        return membershipDao.findActiveByClientId(clientId);
    }
    

    public List<Membership> getExpiringMemberships(LocalDate beforeDate) {
        log.info("Finding memberships expiring before: {}", beforeDate);
        return membershipDao.findExpiringMemberships(beforeDate);
    }
    

    public List<Membership> getMembershipsByType(Membership.MembershipType type) {
        log.info("Finding memberships of type: {}", type);
        return membershipDao.findByType(type);
    }
    

    public Membership saveMembership(Membership membership) throws Exception {
        if (membership.getId() == null) {
            log.info("Creating new membership for client: {}", 
                    membership.getClient() != null ? membership.getClient().getFullName() : "unknown");
        } else {
            log.info("Updating existing membership ID: {}", membership.getId());
        }
        
        Membership savedMembership = membershipDao.save(membership);
        if (savedMembership == null) {
            throw new Exception("Failed to save membership");
        }
        
        return savedMembership;
    }
    

    public void deleteMembership(Long id) throws Exception {
        log.info("Deleting membership with ID: {}", id);
        membershipDao.deleteById(id);
    }
    

    public Membership markAsPaid(Long id) throws Exception {
        log.info("Marking membership ID: {} as paid", id);
        Optional<Membership> membershipOpt = membershipDao.findById(id);
        
        if (membershipOpt.isEmpty()) {
            throw new Exception("Membership not found with ID: " + id);
        }
        
        Membership membership = membershipOpt.get();
        membership.setPaid(true);
        
        return saveMembership(membership);
    }
    

    public List<Membership> getActiveMemberships() {
        log.info("Retrieving all active memberships");
        LocalDate currentDate = LocalDate.now();
        
        return getAllMemberships().stream()
                .filter(m -> m.getEndDate() != null && 
                        (m.getEndDate().isEqual(currentDate) || m.getEndDate().isAfter(currentDate)))
                .toList();
    }
    
  
    public List<Membership> getUnpaidMemberships() {
        log.info("Retrieving all unpaid memberships");
        
        return getAllMemberships().stream()
                .filter(m -> !m.isPaid())
                .toList();
    }
}
