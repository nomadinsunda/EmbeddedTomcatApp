package com.intheeast.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.intheeast.entity.FileEntity;
import com.intheeast.entity.Post;
import com.intheeast.service.EntityCallback;
import com.intheeast.service.FileService;
import com.intheeast.service.PostService;

@Controller
@RequestMapping("/posts/{postId}/files")
public class FileController {
	
	@Value("${file.upload.dir}")
    private String uploadDirPath;

    @Autowired
    private EntityCallback<Post> postCallbackImpl;

    private final FileService fileService;
    private final PostService postService;

    @Autowired
    public FileController(FileService fileService, PostService postService) {
        this.fileService = fileService;
        this.postService = postService;
    }

    // 파일 업로드 처리
    @PostMapping("/upload")
    public String handleFileUpload(@PathVariable Long postId,
                                   @RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        Post post = postService.findById(postId, postCallbackImpl);
        if (post == null) {
            model.addAttribute("error", "Post not found");
            return "redirect:/posts";
        }

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/posts/" + postId;
        }

        try {
            // 서버에 파일을 저장할 경로 지정
//            String uploadDir = System.getProperty("java.io.tmpdir"); // 운영 중에는 실제 경로로 설정 필요
            File destinationFile = new File(uploadDirPath + File.separator + file.getOriginalFilename());
            file.transferTo(destinationFile);

            
            // 파일 정보를 데이터베이스에 저장
            fileService.storeFile(file, post, destinationFile.getAbsolutePath());  // 파일 저장 로직
            redirectAttributes.addFlashAttribute("message", "File uploaded successfully.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "File upload failed!");
            e.printStackTrace();
        }

        return "redirect:/posts/" + postId;
    }

    // 파일 다운로드 처리
    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long postId, @PathVariable Long fileId) {
        FileEntity file = fileService.findById(fileId);
        if (file == null || !file.getPost().getId().equals(postId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            Path filePath = Paths.get(file.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 직접 다운로드 처리
    @GetMapping("/downloadDirect")
    public ResponseEntity<InputStreamResource> downloadDirectFile() throws IOException {
        File file = new File("/path/to/file.txt");
        InputStream resource = new FileInputStream(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(new InputStreamResource(resource));
    }
}
