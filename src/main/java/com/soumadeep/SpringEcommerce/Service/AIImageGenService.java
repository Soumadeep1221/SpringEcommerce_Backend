package com.soumadeep.SpringEcommerce.Service;

import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class AIImageGenService {

    @Autowired
    private ImageModel imageModel;

    public byte[] generateImage(String prompt) {

        OpenAiImageOptions options= OpenAiImageOptions.builder()
                .height(1024)
                .width(1024)
                .quality("standard")
//                .responseFormat("url")
                .responseFormat("b64_json")
                .build();

        ImageResponse response=imageModel.call(new ImagePrompt(prompt,options));
//        String imageUrl=response.getResult().getOutput().getUrl();

//        try {
//            return new URL(imageUrl).openStream().readAllBytes();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }


        String image_b64=response.getResult().getOutput().getB64Json();
        return Base64.getDecoder().decode(image_b64);
    }
}
