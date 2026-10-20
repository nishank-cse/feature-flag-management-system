
"use client";

import { useState } from "react";
import { GoogleLogin, type CredentialResponse } from "@react-oauth/google";
import { useRouter } from "next/navigation";
import { useTheme } from "next-themes";
import { toast } from "sonner";

import { useAuth } from "@/context/AuthContext";
import { ApiError } from "@/lib/api/client";
import config from "@/lib/config";

export function GoogleSignInButton() {
    const router = useRouter();
    const { oauthLogin } = useAuth();
    const { resolvedTheme } = useTheme();
    const [isLoading, setIsLoading] = useState(false);

    async function handleSuccess(credentialResponse: CredentialResponse) {
        if (!credentialResponse.credential) {
            toast.error("Google Sign-In failed", {
                description: "No credential received from Google.",
            });
            return;
        }

        setIsLoading(true);

        try {
            await oauthLogin("google", credentialResponse.credential);
            toast.success("Welcome!", {
                description: "You have successfully signed in with Google.",
            });
            router.push("/dashboard");
        } catch (error) {
            if (error instanceof ApiError) {
                toast.error("Google Sign-In failed", {
                    description: error.message,
                });
            } else {
                toast.error("Google Sign-In failed", {
                    description: "An unexpected error occurred. Please try again.",
                });
            }
        } finally {
            setIsLoading(false);
        }
    }

    function handleError() {
        toast.error("Google Sign-In failed", {
            description: "Could not connect to Google. Please try again.",
        });
    }

    // Prevent the Google OAuth provider error when no client ID exists.
    if (!config.googleClientId) {
        return (
            <p className="text-center text-sm text-gray-500">
                Google Sign-In is not configured. Please use email sign-in.
            </p>
        );
    }

    return (
        <div className="flex w-full justify-center">
            <GoogleLogin
                onSuccess={handleSuccess}
                onError={handleError}
                useOneTap={false}
                theme={resolvedTheme === "dark" ? "filled_black" : "outline"}
                size="large"
                width="100%"
                text="continue_with"
                shape="rectangular"
            />
        </div>
    );
}

export default GoogleSignInButton;
