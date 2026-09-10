import api from "./AxiosInstance";

export const getGithubRepositories = async () => {
    const response =await api.get("/github/repos");
    return response.data;
};

export const getGithubAuthorizationUrl = async () => {
    const response = await api.get("/github/connect");
    return response.data;
}

export const getGithubStatus = async () => {
    const response = await api.get("/github/status");
    return response.data;
};