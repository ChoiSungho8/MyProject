async function get1(bno) {

    const result = await axios.get(`/replies/list/${bno}`)

    return result

}

async function getList({bno, page, size, goLast}){

    const result = await axios.get(`/replies/list/${bno}`, {params: {page, size}})

    if(goLast) {
        const total = result.data.total
        const lastPage = parseInt(Math.ceil(total/size))
        return getList({bno:bno, page:lastPage, size:size})
    }

    return result.data

}

// 새로운 댓글을 등록하는 기능
async function addReply(replyObj) {
    const response = await axios.post(`/replies/`, replyObj)
    return response.data
}

// 특정한 번호의 댓글을 조회하고 수정할 수 있는 기능 구성
async function getReply(rno) {
    const response = await axios.get(`/replies/${rno}`)
    return response.data
}

async function modifyReply(replyObj) {
    const response = await axios.put(`/replies/${replyObj.rno}`, replyObj)
    return response.data
}

async function removeReply(rno) {
    const response = await axios.delete(`/replies/${rno}`)
    return response.data
}