package com.samilozturk.grad_samil.validator

import com.samilozturk.grad_samil.data.AddGalleryItemData

class AddGalleryItemValidator : Validator<AddGalleryItemData> {

    override fun validate(args: AddGalleryItemData) {
        if (args.title.isEmpty()) {
            error("Başlık boş olamaz")
        }
        if (args.mediaUri == null) {
            error("Resim veya video seçilmeli")
        }
        if (args.thumbnailUri == null) {
            error("Resim veya video seçilmeli")
        }
    }


}