export default function SocialLinks() {
  return (
    <div className="social-links" aria-label="Social links">
      <a
        href="https://www.instagram.com/"
        target="_blank"
        rel="noopener noreferrer"
        aria-label="Instagram"
        className="instagram"
      >
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <rect
            x="3"
            y="3"
            width="18"
            height="18"
            rx="5"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
          />
          <circle
            cx="12"
            cy="12"
            r="4"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
          />
          <circle cx="17.5" cy="6.5" r="1.2" fill="currentColor" />
        </svg>
      </a>
      <a
        href="https://www.google.com/"
        target="_blank"
        rel="noopener noreferrer"
        aria-label="Google"
        className="google"
      >
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path
            fill="#4285f4"
            d="M21.6 12.2c0-.7-.1-1.4-.2-2H12v3.8h5.4a4.6 4.6 0 0 1-2 3v2.5h3.2c1.9-1.7 3-4.3 3-7.3Z"
          />
          <path
            fill="#34a853"
            d="M12 22c2.7 0 5-.9 6.6-2.5l-3.2-2.5a6 6 0 0 1-9-3.2H3.1v2.6A10 10 0 0 0 12 22Z"
          />
          <path
            fill="#fbbc05"
            d="M6.4 13.8a6 6 0 0 1 0-3.6V7.6H3.1a10 10 0 0 0 0 8.8l3.3-2.6Z"
          />
          <path
            fill="#ea4335"
            d="M12 6c1.5 0 2.8.5 3.8 1.5l2.9-2.9A9.6 9.6 0 0 0 12 2a10 10 0 0 0-8.9 5.6l3.3 2.6A6 6 0 0 1 12 6Z"
          />
        </svg>
      </a>
      <a
        href="https://www.youtube.com/"
        target="_blank"
        rel="noopener noreferrer"
        aria-label="YouTube"
        className="youtube"
      >
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <rect x="1" y="4" width="22" height="16" rx="5" fill="currentColor" />
          <path d="m10 8 6 4-6 4Z" fill="white" />
        </svg>
      </a>
    </div>
  );
}
