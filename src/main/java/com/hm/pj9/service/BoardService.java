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
    private String uploadDir; // 파일을 저장할 디렉토리 경로

    /*
    * 게시글, 게시글 사진 저장 부분
    * */

    public Post saveBoard(Post post, String author) { // 게시글 저장
        User user = userRepository.findById(author).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        post.setAuthor(user);

        return boardRepository.save(post);
    }


    public void saveFiles(MultipartFile[] files, Post post) throws IOException { // 게시글 사진 저장
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = file.getOriginalFilename(); // 원래 파일 이름
                File destinationFile = new File(uploadDir + File.separator + fileName);
                try {
                    file.transferTo(destinationFile);
                } catch (IOException e) {
                    System.out.println("저장 중 오류 발생: " + e.getMessage());
                }
                boardImageRepository.save(new PostImage(post, fileName));
            }
        }
    }

    /*
    * 게시글 열람 부분
    * */

    public Post getPost(Integer postNum) { //게시물 정보 가져오기
        return boardRepository.findByPostNum(postNum);
    }

    public List<PostImage> getPostImg(Post savedPost) { //게시물 이미지 가져오기
        return boardImageRepository.findByPost(savedPost);
    }

    /*
    * 게시글 삭제 부분
    * */

    @Transactional
    public void deletePost(Integer postNum) { //게시글 삭제
        Post post = boardRepository.findByPostNum(postNum);
        notificationRepository.deleteAllByPostNum(post);
        replyRepository.deleteAllByPostNum(post);
        commentRepository.deleteAllByPostNum(post);
        boardImageRepository.deleteAllByPost(post);
        boardRepository.deleteById(postNum);
    }


    /*
    * 게시글 수정 부분
    * */

    @Transactional
    public void updatePost(Integer postNum, String boardType, String content, String title, MultipartFile[] files) throws IOException { //게시글 수정 후 저장
        Post post = boardRepository.findById(postNum).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        post.setTitle(title); //제목
        post.setBoardType(boardType); //게시판
        post.setContent(content); // 내용

        if (files.length != 0) {
            List<PostImage> images = boardImageRepository.findByPost(post); // 게시글 사진
            if (!images.isEmpty()) { // 서버에 저장한 게시글 사진 삭제
                for (PostImage postImage : images) {
                    File file = new File(uploadDir + File.separator + postImage.getImageUrl());
                    if (file.delete()) {
                        System.out.println("파일이 성공적으로 삭제되었습니다: " + postImage.getImageUrl());
                    } else {
                        System.out.println("파일 삭제에 실패했거나 파일이 존재하지 않습니다: " + postImage.getImageUrl());
                    }
                }
            }
            boardImageRepository.deleteAllByPost(post); //db에 저장된 사진들 삭제
            saveFiles(files, post);
        }

    }



}
