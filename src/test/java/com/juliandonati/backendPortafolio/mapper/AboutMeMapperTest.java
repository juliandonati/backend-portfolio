package com.juliandonati.backendPortafolio.mapper;

import com.juliandonati.backendPortafolio.domain.AboutMe;
import com.juliandonati.backendPortafolio.dto.AboutMeDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class AboutMeMapperTest {
    AboutMeMapper aboutMeMapper = Mappers.getMapper(AboutMeMapper.class);

    @Test
    void testMapAboutMeToAboutMeDtoSuccessfully() {
        Long mockId = 1L;
        String mockTitle = "Sobre mí", mockDesc = "Me encanta programar aplicaciones en Java.", mockImgUrl = "http://bgimage.com",
        mockBtnText = "Presiona aquí", mockBtnUrl = "http://github.com";
        AboutMe mockAboutMe = new AboutMe(mockId,mockTitle,mockDesc,mockImgUrl,mockBtnText,mockBtnUrl,null);

        AboutMeDto result = aboutMeMapper.toDto(mockAboutMe);


        assertAll("Validando todos los campos del mapper",
                ()->assertNotNull(result),
                ()->assertEquals(mockId,result.getId()),
                ()->assertEquals(mockTitle,result.getTitle()),
                ()->assertEquals(mockDesc,result.getDescription()),
                ()->assertEquals(mockImgUrl,result.getBgImgUrl()),
                ()->assertEquals(mockBtnText,result.getButtonText()),
                ()->assertEquals(mockBtnUrl,result.getButtonUrl()));
    }

    @Test
    void testMapAboutMeDtoToAboutMeEntitySuccessfully() {
        Long mockId = 1L;
        String mockTitle = "Sobre mí", mockDesc = "Me encanta programar aplicaciones en Java.", mockImgUrl = "http://bgimage.com",
                mockBtnText = "Presiona aquí", mockBtnUrl = "http://github.com";
        AboutMeDto mockAboutMeDto = new AboutMeDto(mockId,mockTitle,mockDesc,mockImgUrl,mockBtnText,mockBtnUrl);

        AboutMe result = aboutMeMapper.toEntity(mockAboutMeDto);

        assertAll("Validando todos los campos del mapper",
                ()->assertNotNull(result),
                ()->assertEquals(mockId,result.getId()),
                ()->assertEquals(mockTitle,result.getTitle()),
                ()->assertEquals(mockDesc,result.getDescription()),
                ()->assertEquals(mockImgUrl,result.getBgImgUrl()),
                ()->assertEquals(mockBtnText,result.getButtonText()),
                ()->assertEquals(mockBtnUrl,result.getButtonUrl()));
    }

    @Test
    void testUpdateAboutMeEntitySuccessfully() {
        Long mockId = 1L;
        AboutMe mockAboutMe = new AboutMe(mockId,"Sobre mí","Me encanta programar aplicaciones en Java.",
                "http://bgimage.com","Presiona aquí","http://github.com",null);

        String mockTitle = "¿Sabías que...?", mockDesc = "Soy muy bueno codificando front-ends", mockImgUrl="http://fondoimagen.com",
                mockBtnText = "Apretame", mockBtnUrl = "http://linkedin.com";
        AboutMeDto mockAboutMeDto = new AboutMeDto(4L,mockTitle,mockDesc,mockImgUrl,mockBtnText,mockBtnUrl);
        // Id diferente para comprobar que no cambie, por más que le mande otra.

        AboutMe result = aboutMeMapper.updateEntity(mockAboutMeDto,mockAboutMe);

        assertAll("Validando todos los campos del mapper",
                ()->assertNotNull(result),
                ()->assertEquals(mockId,result.getId()),
                ()->assertEquals(mockTitle,result.getTitle()),
                ()->assertEquals(mockDesc,result.getDescription()),
                ()->assertEquals(mockImgUrl,result.getBgImgUrl()),
                ()->assertEquals(mockBtnText,result.getButtonText()),
                ()->assertEquals(mockBtnUrl,result.getButtonUrl()));
    }
}