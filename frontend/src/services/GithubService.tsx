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

export const getGithubRepository=async()=>{
      const response= await api.post("repositories/${repoId}/commits");
      return response.data;
}

export const getGithubRepositoryBranches = async (repoId: number) => {
    const response = await api.get(`/repositories/${repoId}/branches`);
    return response.data;
};

export const getGithubRepositoryCommits = async (repoId: number,branch: string) => {

    const response = await api.get(
        `/repositories/${repoId}/commits`,
        {
            params: {
                branch
            }
        }
    );

    return response.data;
};