package sk.stuba.fei.uim.vsa.pr2.resources.responses.factories;

import sk.stuba.fei.uim.vsa.pr2.resources.responses.Dto;

public interface ResponseFactory<R, T extends Dto> {

    T transformToDto(R entity);

    R transformToEntity(T dto);

}