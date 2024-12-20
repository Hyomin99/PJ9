package com.hm.pj9.service;

import com.hm.pj9.model.*;
import com.hm.pj9.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class BoardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardImageRepository boardImageRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Value("${file.upload-dir}")
    private String uploadDir = "resources/static/img"; // 파일을 저장할 디렉토리 경로

    public Post saveBoard(Post post, String author) { // 게시글 저장
        User user = userRepository.findById(author).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        post.setAuthor(user);

        return boardRepository.save(post);
    }

    public void saveComment(Comment comment, String commenter_id, Integer postNum) { // 댓글 저장
        User commenterUser = userRepository.findById(commenter_id).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")); //댓글 작성자
        Post post = boardRepository.findById(postNum).orElseThrow(() -> new RuntimeException("게시판 번호를 찾을 수 없습니다."));
        comment.setCommenterId(commenterUser);
        comment.setPostNum(post);


        User postAuthorUser = post.getAuthor(); //게시글 작성자


        Comment savedComment = commentRepository.save(comment); //댓글 저장

        String commenter = commenterUser.getUserId(); // 댓글 작성자
        String postAuthor = postAuthorUser.getUserId(); // 게시물 작성자

        if (!commenter.equals(postAuthor)) { //댓글에 해당하는 게시글 알림 저장
            Notification no = new Notification();
            no.setPostNum(post);
            no.setCommentNum(savedComment);
            no.setTargetId(postAuthorUser);
            no.setNotificationType("post_notice");
            no.setReadStatus((byte) 0);
            notificationRepository.save(no);
        }


    }

    public void saveReply(Reply reply, String replier_id, Integer postNum, Integer commentNum) { // 대 댓글 저장

        User user = userRepository.findById(replier_id).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Post post = boardRepository.findById(postNum).orElseThrow(() -> new RuntimeException("게시판 번호를 찾을 수 없습니다."));
        Comment comment = commentRepository.findById(commentNum).orElseThrow(() -> new RuntimeException("댓글 번호를 찾을 수 없습니다."));


        reply.setReplierId(user);
        reply.setPostNum(post);
        reply.setCommentNum(comment);


        Reply savedReply = replyRepository.save(reply);
        String commenter = comment.getCommenterId().getUserId(); //댓글 작성자
        String replier = reply.getReplierId().getUserId(); // 대댓글 작성자
        String boardAuthor = post.getAuthor().getUserId(); // 게시물 작성자


        if (!commenter.equals(replier) && !boardAuthor.equals(replier)) { //
            notificationRepository.save(replyToComment(post, savedReply, comment));
        }


    }


    public void saveFiles(MultipartFile[] files, Post post) throws IOException { // 게시글 사진 저장

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = file.getOriginalFilename(); // 원래 파일 이름
                File destinationFile = new File(uploadDir + File.separator + fileName);
                file.transferTo(destinationFile); // 파일 저장


                PostImage postImage = new PostImage();
                postImage.setPost(post); // 게시글 번호 설정
                postImage.setImageUrl(fileName); // 이미지 URL 설정

                boardImageRepository.save(postImage);
                System.out.println("Saved PostImage with ID: " + postImage.getImageNum() + " and PostNum: " + postImage.getPost().getPostNum());
            }
        }
    }

    public Post getPost(Integer postNum) { //게시물 정보 가져오기
        return boardRepository.findByPostNum(postNum);
    }

    public List<PostImage> getPostImg(Post savedPost) { //게시물 이미지 가져오기
        return boardImageRepository.findByPost(savedPost);
    }

    public List<Comment> getComments(Post broughtPost) { //게시물 댓글 가져오기
        return commentRepository.findByPostNum(broughtPost);
    }

    public List<Reply> getReplies(Post broughtPost) { //게시물  대댓글 가져오기
        return replyRepository.findByPostNum(broughtPost);
    }

    public List<Post> getAllPosts() {
        return boardRepository.findAll();
    }


    public Notification replyToComment(Post post, Reply savedReply, Comment comment) { //댓글에 대댓글을 남겼을 때

        Notification no = new Notification();
        no.setPostNum(post); //게시물 번호
        no.setReplyNum(savedReply); // 대댓글 번호
        no.setTargetId(comment.getCommenterId()); //대댓글을 달고자 하는 댓글의 댓글 작성자
        no.setNotificationType("comment_notice");
        no.setReadStatus((byte) 0);

        return no;
    }

    public List<Post> getPostByTitle(String content) { //제목만 검색
        return boardRepository.findByTitleContaining(content);

    }

    public List<Post> getPostByTitleOrContent(String content) { // 제목 또는 내용으로 검색
        return boardRepository.findByTitleContainingOrContentContaining(content,content);
    }


    public List<Post> getPostById(String targetUserId) { // 제목 또는 내용으로 검색
        User user = userRepository.findById(targetUserId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return boardRepository.findAllByAuthor(user);
    }

    @Transactional
    public void deleteComment(Integer commentNum){ //댓글 삭제
        Comment comment = commentRepository.findById(commentNum).orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        notificationRepository.deleteAllByCommentNum(comment); // 알림 삭제
        replyRepository.deleteAllByCommentNum(comment); // 대댓글 삭제
        commentRepository.deleteById(commentNum);
    }

    @Transactional
    public void deleteReply(Integer replyNum){ //대댓글 삭제
        Reply reply = replyRepository.findById(replyNum).orElseThrow(() -> new RuntimeException("대댓글을 찾을 수 없습니다."));
        notificationRepository.deleteAllByReplyNum(reply); //알림 삭제
        replyRepository.deleteById(replyNum);
    }

    @Transactional
    public void deletePost(Integer postNum){ //게시글 삭제
        Post post = boardRepository.findByPostNum(postNum);
        notificationRepository.deleteAllByPostNum(post);
        replyRepository.deleteAllByPostNum(post);
        commentRepository.deleteAllByPostNum(post);
        boardImageRepository.deleteAllByPost(post);
        boardRepository.deleteById(postNum);
    }

    @Transactional
    public void updatePost(Integer postNum, String boardType, String content, String title,MultipartFile[] files)throws IOException{
        Post post = boardRepository.findById(postNum).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        post.setTitle(title); //제목
        post.setBoardType(boardType); //게시판
        post.setContent(content); // 내용

        List<PostImage> images = boardImageRepository.findByPost(post); // 게시글 사진
        if(!images.isEmpty()){ // 서버에 저장한 게시글 사진 삭제
            for(PostImage postImage : images){
                File file = new File(uploadDir +File.separator + postImage.getImageUrl());
                if (file.delete()) {
                    System.out.println("파일이 성공적으로 삭제되었습니다: " + postImage.getImageUrl());
                } else {
                    System.out.println("파일 삭제에 실패했거나 파일이 존재하지 않습니다: " + postImage.getImageUrl());
                }
            }
        }

        boardImageRepository.deleteAllByPost(post); //db에 저장된 사진들 삭제


        for (MultipartFile file : files) { // 사진들 다시 저장
            if (!file.isEmpty()) {
                String fileName = file.getOriginalFilename(); // 원래 파일 이름
                File destinationFile = new File(uploadDir + File.separator + fileName);
                file.transferTo(destinationFile); // 파일 저장
                PostImage postImage = new PostImage();
                postImage.setPost(post); // 게시글 번호 설정
                postImage.setImageUrl(fileName); // 이미지 URL 설정
                boardImageRepository.save(postImage);
            }
        }
    }




}
