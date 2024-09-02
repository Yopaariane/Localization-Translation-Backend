package com.myapp.localizationApp.service;

import com.myapp.localizationApp.configuration.ResourceNotFoundException;
import com.myapp.localizationApp.dto.CommentsDto;
import com.myapp.localizationApp.dto.TermsDto;
import com.myapp.localizationApp.entity.Comments;
import com.myapp.localizationApp.entity.Terms;
import com.myapp.localizationApp.entity.User;
import com.myapp.localizationApp.repository.CommentsRepository;
import com.myapp.localizationApp.repository.TermsRepository;
import com.myapp.localizationApp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class CommentsService {
    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TermsRepository termsRepository;
    @Autowired
    private UserRepository userRepository;

    public CommentsDto createComment(CommentsDto commentsDto){
        Comments comments = modelMapper.map(commentsDto, Comments.class);
        Terms terms = termsRepository.findById(commentsDto.getTermId())
                .orElseThrow(() -> new ResourceNotFoundException("Term not found with id:" + commentsDto.getTermId()));
        User user = userRepository.findById(commentsDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("user not found with id: " + commentsDto.getUserId()));
        comments.setTerm(terms);
        comments.setUser(user);

        Comments savedComment = commentsRepository.save(comments);
        return modelMapper.map(savedComment, CommentsDto.class);
    }

    public CommentsDto updateComment(Long id, CommentsDto commentsDto){
        Comments comments = commentsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        comments.setComment(commentsDto.getComment());

        Terms terms = termsRepository.findById(commentsDto.getTermId())
                .orElseThrow(() -> new ResourceNotFoundException("Term not found with id:" + commentsDto.getTermId()));
        User user = userRepository.findById(commentsDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("user not found with id: " + commentsDto.getUserId()));
        comments.setTerm(terms);
        comments.setUser(user);

        Comments updatedComment = commentsRepository.save(comments);
        return  modelMapper.map(updatedComment, CommentsDto.class);
    }

    public void deleteComment(Long id){
        commentsRepository.deleteById(id);
    }

    public List<CommentsDto> findCommentsByTermId(Long termId) {
        List<Comments> comments = commentsRepository.findByTermId(termId);
        modelMapper.typeMap(Comments.class, CommentsDto.class).addMappings(mapper ->{
            mapper.map(Comments::getCreated_at, CommentsDto::setCreatedAt);
        });
        return  comments.stream().map(comments1 -> modelMapper.map(comments1, CommentsDto.class))
                .collect(Collectors.toList());
    }

    public List<CommentsDto> findCommentByTermIdAndUserId(Long termId, Long userId) {
        List<Comments> comments = commentsRepository.findCommentByTermIdAndUserId(termId, userId);
        modelMapper.typeMap(Comments.class, CommentsDto.class).addMappings(mapper ->{
            mapper.map(Comments::getCreated_at, CommentsDto::setCreatedAt);
        });
        return  comments.stream().map(comments1 -> modelMapper.map(comments1, CommentsDto.class))
                .collect(Collectors.toList());
    }
}
