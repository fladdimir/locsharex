import { OAUTH_LOGIN_URL } from "../login/Login";

export async function fetchX(
  url: string,
  method: string,
  body?: any
): Promise<any> {
  return fetch(url, {
    method,
    body: body ? JSON.stringify(body) : undefined,
    headers: body ? { "Content-Type": "application/json" } : undefined,
  }).then((response) => {
    if (!response.ok) {
      if ([401, 403].includes(response.status)) {
        window.location.href = OAUTH_LOGIN_URL; // retry login
        return;
      }
      console.error(response);
      return;
    }
    return response.json();
  });
}
