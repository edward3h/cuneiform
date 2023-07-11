@param String key
@param String display
@param boolean exclude = false
@param java.util.List<com.ordoacerbus.cuneiform.Keyword> boxKeywords = java.util.List.of()

<div class="form-control bg-base-100 px-2 rounded mb-4 toggle-box ${com.ordoacerbus.cuneiform.Util.keywordsClasses(boxKeywords)}">
    <label class="label cursor-pointer">
        <span class="label-text">${display}</span>
        <input type="checkbox" class="checkbox ${exclude ? "checkbox-accent" : "checkbox-primary"}" name="${key}" value="${exclude ? "exclude" : "include"}"/>
    </label>
</div>
